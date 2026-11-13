import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../models/user';
import { tap, catchError, of } from 'rxjs';
import { API_URL } from '../core/api.config';
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) {}

  currentUser = signal<User | null>(null);

  signup(name: string, email: string, password: string) {
    return this.http.post(`${API_URL}/auth/register`, {
      name,
      email,
      password,
    });
  }

  login(email: string, password: string) {
    return this.http.post(`${ API_URL } /auth/login`, {
      email,
      password,
    });
  }

  logout() {
    return this.http
      .post(`${API_URL}/auth/logout`, {})
      .pipe(tap(() => this.currentUser.set(null)));
  }

  getCurrentUser() {
    return this.http.get(`${API_URL}/ users / me`);
  }

  loadCurrentUser() {
    return this.http.get<User>(`${API_URL}/users/me`).pipe(
      tap((user) => this.currentUser.set(user)),
      catchError(() => {
        this.currentUser.set(null);
        return of(null);
      }),
    );
  }
}
