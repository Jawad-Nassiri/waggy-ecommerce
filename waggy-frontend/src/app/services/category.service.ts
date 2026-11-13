import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Category } from '../models/category';
import { API_URL } from '../core/api.config';


@Injectable({
  providedIn: 'root',
})
export class CategoryService {
    constructor(private http: HttpClient) { }
    
    categoryPath = `${API_URL}/categories`;

    getCategories() {
        return this.http.get<Category[]>(this.categoryPath);
    }

}
