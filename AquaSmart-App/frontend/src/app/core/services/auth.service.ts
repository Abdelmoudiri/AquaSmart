import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

// Environnement (idéalement dans src/environments/environment.ts)
const API_URL = 'http://localhost:8080/api/users/auth'; // Gateway URL

export interface AuthResponse {
    token: string;
    type: string;
    id: number;
    username: string;
    email: string;
    roles: string[];
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);
    public currentUser$ = this.currentUserSubject.asObservable();

    // Utilisation de Signal pour le state moderne
    public isAuthenticated = signal<boolean>(false);

    constructor(private http: HttpClient) {
        this.loadUserFromStorage();
    }

    private loadUserFromStorage() {
        const storedUser = localStorage.getItem('currentUser');
        if (storedUser) {
            const user = JSON.parse(storedUser);
            this.currentUserSubject.next(user);
            this.isAuthenticated.set(true);
        }
    }

    register(userData: any): Observable<any> {
        return this.http.post(`${API_URL}/register`, userData);
    }

    login(credentials: any): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${API_URL}/login`, credentials).pipe(
            tap(response => {
                localStorage.setItem('currentUser', JSON.stringify(response));
                localStorage.setItem('token', response.token); // Si besoin séparé
                this.currentUserSubject.next(response);
                this.isAuthenticated.set(true);
            })
        );
    }

    logout() {
        localStorage.removeItem('currentUser');
        localStorage.removeItem('token');
        this.currentUserSubject.next(null);
        this.isAuthenticated.set(false);
    }

    getToken(): string | null {
        return localStorage.getItem('token');
    }
}
