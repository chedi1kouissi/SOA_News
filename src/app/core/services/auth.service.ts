import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private currentUserSubject = new BehaviorSubject<any>(null);
    public currentUser$ = this.currentUserSubject.asObservable();

    constructor(private apiService: ApiService) {
        this.loadUserFromStorage();
    }

    private loadUserFromStorage() {
        const userId = localStorage.getItem('userId');
        const username = localStorage.getItem('username');
        if (userId && username) {
            this.currentUserSubject.next({ id: userId, username });
        }
    }

    login(credentials: any): Observable<any> {
        return this.apiService.post('/users/login', credentials).pipe(
            tap((response: any) => {
                if (response.userId) {
                    localStorage.setItem('userId', response.userId);
                    localStorage.setItem('username', response.username);
                    this.currentUserSubject.next({ id: response.userId, username: response.username });
                }
            })
        );
    }

    register(user: any): Observable<any> {
        return this.apiService.post('/users/register', user);
    }

    logout() {
        localStorage.removeItem('userId');
        localStorage.removeItem('username');
        this.currentUserSubject.next(null);
    }

    get currentUserValue() {
        return this.currentUserSubject.value;
    }

    isAuthenticated(): boolean {
        return !!this.currentUserSubject.value;
    }
}
