import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../models/user';
import { tap, catchError, of } from 'rxjs';
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) {}

  currentUser = signal<User | null>(null);

  signup(name: string, email: string, password: string) {
    return this.http.post('http://localhost:8080/auth/register', {
      name,
      email,
      password,
    });
  }

  login(email: string, password: string) {
    return this.http.post('http://localhost:8080/auth/login', {
      email,
      password,
    });
  }

  logout() {
    return this.http.post('http://localhost:8080/auth/logout', {});
  }

  getCurrentUser() {
    return this.http.get('http://localhost:8080/users/me');
  }

  loadCurrentUser() {
    return this.http.get<User>('http://localhost:8080/users/me').pipe(
      tap((user) => this.currentUser.set(user)),
      catchError(() => {
        this.currentUser.set(null);
        return of(null);
      }),
    );
  }
}
