import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../models/user';
import { API_URL } from '../core/api.config';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) {}

  userPath = `${API_URL}/users`;

  getAllUsers() {
    return this.http.get<User[]>(this.userPath);
  }

  updateUser(id: number, data: User) {
    return this.http.put<User>(`${this.userPath}/${id}`, data);
  }

  findUserById(id: number) {
    return this.http.get<User>(this.userPath + '/' + id);
  }

  deleteUser(id: number) {
    return this.http.delete(this.userPath + '/' + id);
  }
}
