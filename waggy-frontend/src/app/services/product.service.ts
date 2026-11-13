import { HttpClient } from '@angular/common/http';
import { Product } from '../models/product';
import { Injectable } from '@angular/core';
import { API_URL } from '../core/api.config';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  constructor(private http: HttpClient) {}

  private productPath = `${API_URL}/products`;

  getProducts() {
    return this.http.get<Product[]>(this.productPath);
  }

  getProductById(id: number) {
    return this.http.get<Product>(`${this.productPath}/${id}`);
  }

  updateProduct(id: number, data: FormData) {
    return this.http.put<Product>(`${this.productPath}/${id}`, data);
  }

  deleteProduct(id: number) {
    return this.http.delete(this.productPath + '/' + id);
  }

  addProduct(formData: FormData) {
    return this.http.post<Product>(this.productPath, formData);
  }
}
