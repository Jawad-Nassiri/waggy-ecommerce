import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Category } from '../models/category';


@Injectable({
  providedIn: 'root',
})
export class CategoryService {
    constructor(private http: HttpClient) { }
    
    categoryPath = 'http://localhost:8080/categories';

    getCategories() {
        return this.http.get<Category[]>(this.categoryPath);
    }

}
