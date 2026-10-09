import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product } from '../models/product';
import { Cart } from '../models/cart';
import { tap } from 'rxjs';
import { Order } from '../models/order';
import { catchError, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  constructor(private http: HttpClient) {}

  path = 'http://localhost:8080/carts';
  ordersPath = 'http://localhost:8080/orders';
  paymentsPath = 'http://localhost:8080/payments';
  cartCount = signal(0);

  addToCart(product: Product, quantity: number) {
    return this.http.post(this.path + '/items', {
      productId: product.id,
      quantity: quantity,
    });
  }

  getMyCart() {
    return this.http.get<Cart>(this.path + '/me').pipe(
      tap((cart) => this.cartCount.set(cart.items.length)),
      catchError(() => {
        this.cartCount.set(0);
        return of({ items: [] } as unknown as Cart);
      }),
    );
  }

  clearCartCount() {
    this.cartCount.set(0);
  }

  updateCartItem(productId: number, quantity: number) {
    return this.http.put<Cart>(this.path + '/items/' + productId, { productId, quantity });
  }

  removeItemFromCart(productId: number) {
    return this.http.delete<Cart>(this.path + '/items/' + productId);
  }

  clearCart() {
    this.clearCartCount();
    return this.http.delete<void>(this.path + '/me/items');
  }

  createOrder(items: { productId: number; quantity: number }[]) {
    return this.http.post<{ id: number }>(this.ordersPath, { items });
  }

  createPayment(orderId: number) {
    return this.http.post<{ checkoutUrl: string }>(this.paymentsPath, { orderId });
  }

  getOrders() {
    return this.http.get<Order[]>(this.ordersPath + '/me');
  }
}
