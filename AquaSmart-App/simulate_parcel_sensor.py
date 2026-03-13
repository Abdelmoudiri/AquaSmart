#!/usr/bin/env python3
import argparse
import json
import random
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from datetime import datetime
from typing import Any


SOIL_DEFAULTS = {
    "CLAY": 55.0,
    "LOAMY": 45.0,
    "SILTY": 50.0,
    "SANDY": 30.0,
}


class ApiClient:
    def __init__(self, base_url: str):
        self.base_url = base_url.rstrip("/")

    def get_json(self, path: str, params: dict | None = None):
        url = self._build_url(path, params)
        request = urllib.request.Request(url, method="GET")
        return self._send(request)

    def patch_json(self, path: str, params: dict | None = None):
        url = self._build_url(path, params)
        request = urllib.request.Request(url, method="PATCH")
        return self._send(request)

    def _build_url(self, path: str, params: dict | None):
        query = urllib.parse.urlencode(params or {})
        url = f"{self.base_url}{path}"
        return f"{url}?{query}" if query else url

    def _send(self, request: urllib.request.Request):
        try:
            with urllib.request.urlopen(request, timeout=20) as response:
                return json.loads(response.read().decode("utf-8"))
        except urllib.error.HTTPError as exc:
            payload = exc.read().decode("utf-8", errors="replace")
            raise RuntimeError(f"HTTP {exc.code} on {request.full_url}: {payload}") from exc
        except urllib.error.URLError as exc:
            raise RuntimeError(f"Request failed for {request.full_url}: {exc}") from exc


def estimate_initial_moisture(parcel: dict, initial_override: float | None) -> float:
    if initial_override is not None:
        return clamp(initial_override)

    current = parcel.get("currentMoisture")
    if isinstance(current, (int, float)):
        return clamp(float(current))

    moisture_min = parcel.get("optimalMoistureMin")
    moisture_max = parcel.get("optimalMoistureMax")
    if isinstance(moisture_min, (int, float)) and isinstance(moisture_max, (int, float)):
        return clamp((float(moisture_min) + float(moisture_max)) / 2)
    if isinstance(moisture_min, (int, float)):
        return clamp(float(moisture_min))
    if isinstance(moisture_max, (int, float)):
        return clamp(float(moisture_max))

    soil_type = str(parcel.get("soilType") or "").upper()
    return SOIL_DEFAULTS.get(soil_type, 40.0)


def evolve_moisture(current: float, weather: dict | None) -> float:
    temperature = as_float(weather, "temperature")
    humidity = as_float(weather, "humidity")
    wind_speed = as_float(weather, "windSpeed")
    rain_1h = as_float(weather, "rain1h")
    description = str((weather or {}).get("weatherDescription") or "").lower()

    moisture = current
    evaporation = 0.6
    if temperature is not None:
        evaporation += max(0.0, (temperature - 22.0) * 0.08)
    if wind_speed is not None:
        evaporation += min(wind_speed, 20.0) * 0.03
    if humidity is not None:
        evaporation -= max(0.0, (humidity - 55.0) * 0.02)

    rain_gain = 0.0
    if rain_1h is not None and rain_1h > 0:
        rain_gain += min(8.0, rain_1h * 4.0)
    elif "rain" in description or "pluie" in description:
        rain_gain += 2.0

    noise = random.uniform(-0.5, 0.5)
    moisture = moisture - evaporation + rain_gain + noise
    return clamp(moisture)


def clamp(value: float) -> float:
    return round(max(5.0, min(95.0, value)), 1)


def as_float(payload: dict | None, key: str):
    if not payload:
        return None
    value = payload.get(key)
    return float(value) if isinstance(value, (int, float)) else None


def print_cycle(parcel: dict, weather: dict | None, recommendation: dict, moisture: float):
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    reasons = recommendation.get("reasons") or []
    warnings = recommendation.get("warnings") or []
    temperature = as_float(weather, "temperature")
    humidity = as_float(weather, "humidity")

    print(f"[{timestamp}] Parcel {parcel['id']} ({parcel.get('name', 'Unnamed')})")
    print(f"  soil moisture: {moisture}%")
    print(f"  weather: temp={temperature if temperature is not None else 'n/a'}C humidity={humidity if humidity is not None else 'n/a'}%")
    print(f"  recommendation: shouldIrrigate={recommendation.get('shouldIrrigate')} water={recommendation.get('recommendedWaterAmount')}L duration={recommendation.get('recommendedDurationMinutes')}min confidence={recommendation.get('confidenceScore')}%")
    if reasons:
        print(f"  reasons: {', '.join(reasons)}")
    if warnings:
        print(f"  warnings: {', '.join(warnings)}")
    print("")


def normalize_parcel_ids(parcel_ids: str | None) -> list[int]:
    if not parcel_ids:
        return []
    return [int(value.strip()) for value in parcel_ids.split(",") if value.strip()]


def load_target_parcels(client: ApiClient, farm_id: int, parcel_ids: list[int], all_parcels: bool) -> list[dict[str, Any]]:
    if all_parcels:
        parcels = client.get_json(f"/farms/{farm_id}/parcels")
        if not isinstance(parcels, list) or not parcels:
            raise RuntimeError(f"No parcels found for farm {farm_id}")
        return parcels

    if not parcel_ids:
        raise RuntimeError("Provide --parcel-id, --parcel-ids, or --all-parcels")

    parcels: list[dict[str, Any]] = []
    for parcel_id in parcel_ids:
        try:
            parcels.append(client.get_json(f"/farms/{farm_id}/parcels/{parcel_id}"))
        except RuntimeError as exc:
            print(f"Skipping parcel {parcel_id}: {exc}", file=sys.stderr)
    if not parcels:
        raise RuntimeError(f"No valid parcels found for farm {farm_id}")
    return parcels


def resolve_coordinates(parcel: dict, farm: dict) -> tuple[float | None, float | None]:
    latitude = parcel.get("latitude") if parcel.get("latitude") is not None else farm.get("latitude")
    longitude = parcel.get("longitude") if parcel.get("longitude") is not None else farm.get("longitude")
    return latitude, longitude


def main():
    parser = argparse.ArgumentParser(description="Simulate parcel soil moisture and query irrigation recommendations.")
    parser.add_argument("--base-url", default="http://localhost:8080/api", help="Gateway API base URL")
    parser.add_argument("--farm-id", type=int, required=True, help="Farm id")
    parser.add_argument("--parcel-id", type=int, help="Single parcel id")
    parser.add_argument("--parcel-ids", help="Comma-separated parcel ids")
    parser.add_argument("--all-parcels", action="store_true", help="Simulate all parcels of the farm")
    parser.add_argument("--interval-seconds", type=int, default=15, help="Seconds between cycles")
    parser.add_argument("--iterations", type=int, default=0, help="Number of cycles, 0 for infinite")
    parser.add_argument("--initial-moisture", type=float, default=None, help="Optional initial soil moisture override")
    args = parser.parse_args()

    client = ApiClient(args.base_url)
    requested_ids = normalize_parcel_ids(args.parcel_ids)
    if args.parcel_id is not None:
        requested_ids.append(args.parcel_id)

    try:
        farm = client.get_json(f"/farms/{args.farm_id}")
        parcels = load_target_parcels(client, args.farm_id, requested_ids, args.all_parcels)
    except RuntimeError as exc:
        print(exc, file=sys.stderr)
        return 1

    parcel_states: dict[int, dict[str, Any]] = {}
    for parcel in parcels:
        latitude, longitude = resolve_coordinates(parcel, farm)
        if latitude is None or longitude is None:
            print(f"Missing coordinates for parcel {parcel.get('id')}. Skipping.", file=sys.stderr)
            continue
        parcel_states[parcel["id"]] = {
            "parcel": parcel,
            "latitude": latitude,
            "longitude": longitude,
            "moisture": estimate_initial_moisture(parcel, args.initial_moisture),
        }

    if not parcel_states:
        print("No parcel has usable coordinates. Cannot simulate recommendation flow.", file=sys.stderr)
        return 1

    cycle = 0

    while args.iterations == 0 or cycle < args.iterations:
        cycle += 1
        for state in parcel_states.values():
            parcel = state["parcel"]
            latitude = state["latitude"]
            longitude = state["longitude"]

            try:
                weather = client.get_json("/weather/current", {"lat": latitude, "lon": longitude})
            except RuntimeError:
                weather = None

            state["moisture"] = evolve_moisture(state["moisture"], weather)

            try:
                client.patch_json(
                    f"/farms/{args.farm_id}/parcels/{parcel['id']}/moisture",
                    {"moisture": state['moisture']},
                )
                recommendation = client.get_json(
                    "/irrigation/recommendation",
                    {
                        "parcelId": parcel["id"],
                        "farmId": args.farm_id,
                        "latitude": latitude,
                        "longitude": longitude,
                        "soilMoisture": state["moisture"],
                    },
                )
            except RuntimeError as exc:
                print(exc, file=sys.stderr)
                return 1

            print_cycle(parcel, weather, recommendation, state["moisture"])

        if args.iterations != 0 and cycle >= args.iterations:
            break
        time.sleep(args.interval_seconds)

    return 0


if __name__ == "__main__":
    raise SystemExit(main())