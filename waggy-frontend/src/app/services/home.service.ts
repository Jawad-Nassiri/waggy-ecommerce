import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Product } from '../models/product';
import { API_URL } from '../core/api.config';

@Injectable({
  providedIn: 'root',
})
export class HomeService {
  constructor(private http: HttpClient) {}

  path = `${API_URL}/products/category/`;

  getProductsByCategoryId(categoryId: number) {
    return this.http.get<Product[]>(this.path + categoryId);
  }
}
