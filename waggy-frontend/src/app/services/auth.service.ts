import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
  
export class AuthService {

  constructor(private http: HttpClient) {}

  currentUser = signal<any>(null);

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
    this.http.get('http://localhost:8080/users/me').subscribe({
      next: (user) => {
        this.currentUser.set(user);
      },
      error: () => {
        this.currentUser.set(null);
      },
    });
  }
}
