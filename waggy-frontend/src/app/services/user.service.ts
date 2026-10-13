import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) {}

  userPath = 'http://localhost:8080/users';

  getAllUsers() {
    return this.http.get<User[]>(this.userPath);
  }

  updateUser(id: number, data: User) {
    return this.http.put<User>(`${this.userPath}/${id}`, data);
    }
    
    findUserById(id: number) {
        return this.http.get<User>(this.userPath +'/' + id)
    }
}
