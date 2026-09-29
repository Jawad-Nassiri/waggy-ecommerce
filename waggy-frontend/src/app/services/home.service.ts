import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Product } from '../models/product';

@Injectable({
  providedIn: 'root',
})
export class HomeService {
  constructor(private http: HttpClient) {}

  path = 'http://localhost:8080/products/category/';

  getProductsByCategoryId(categoryId: number) {
    return this.http.get<Product[]>(this.path + categoryId);
  }
}
