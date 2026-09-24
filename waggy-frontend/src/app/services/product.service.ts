import { HttpClient } from '@angular/common/http';
import { Product } from '../models/product';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  constructor(private http: HttpClient) {}

  private apiUrl = 'http://localhost:8080/products';

  getProducts() {
    return this.http.get<Product[]>(this.apiUrl);
  }
}
