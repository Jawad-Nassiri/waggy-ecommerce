import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
  
export class Signup {

  constructor(private http: HttpClient) {}

  signup(name: string, email: string, password: string) {
    return this.http.post('http://localhost:8080/auth/register', {
      name,
      email,
      password,
    });
  }
}
