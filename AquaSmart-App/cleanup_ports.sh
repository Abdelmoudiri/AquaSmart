#!/bin/bash
PORTS="8761 8085 3306 3308"

echo "=== Port Cleanup Script ==="

for PORT in $PORTS; do
  echo "Checking port $PORT..."
  PID=$(sudo lsof -t -i:$PORT)
  if [ -n "$PID" ]; then
    echo "Killing process $PID on port $PORT..."
    sudo kill -9 $PID
  else
    echo "Port $PORT is free."
  fi
done

echo "=== Cleanup Complete ==="
sudo lsof -i :8761
sudo lsof -i :8085
sudo lsof -i :3306
sudo lsof -i :3308
