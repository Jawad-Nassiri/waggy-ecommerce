import { HttpClient } from '@angular/common/http';
import { Product } from '../models/product';
import { Injectable } from '@angular/core';
@Injectable({
  providedIn: 'root',
})
export class ProductService {
  constructor(private http: HttpClient) {}

  private productPath = 'http://localhost:8080/products';

  getProducts() {
    return this.http.get<Product[]>(this.productPath);
  }

  getProductById(id: number) {
    return this.http.get<Product>(`${this.productPath}/${id}`);
  }

  updateProduct(id: number, data: FormData) {
    return this.http.put<Product>(`${this.productPath}/${id}`, data);
  }

  
}
