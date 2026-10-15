import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Order } from '../models/order';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  constructor(private http: HttpClient) {}

  ordersPath = 'http://localhost:8080/orders';

  getOrders() {
    return this.http.get<Order[]>(this.ordersPath + '/me');
  }

  createOrder(items: { productId: number; quantity: number }[]) {
    return this.http.post<{ id: number }>(this.ordersPath, { items });
  }

  getOrdersCount() {
    return this.http.get<number>(this.ordersPath + '/count');
  }

  getAllOrders() {
    return this.http.get<Order[]>(this.ordersPath);
  }

  deleteOrder(id: number) {
    return this.http.delete(this.ordersPath + '/' + id);
  }
}
