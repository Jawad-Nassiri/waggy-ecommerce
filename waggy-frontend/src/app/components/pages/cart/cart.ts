import { Component, signal } from '@angular/core';
import { Banner } from '../../banner/banner';
import { CartService } from '../../../services/cart.service';
import { CartItem } from '../../../models/cart-item';
import { Cart } from '../../../models/cart';
import { RouterLink } from '@angular/router';
import { Toast } from '../../toast/toast';

@Component({
  selector: 'app-cart',
  imports: [Banner, RouterLink, Toast],
  templateUrl: './cart.html',
  styleUrl: './cart.css',
})
export class CartComponent {
  cartItems = signal<CartItem[]>([]);
  productQty = 0;

  // toast
  showToast = signal(false);
  toastTitle = '';
  toastMessage = '';
  toastType = '';

  constructor(private cartService: CartService) {}

  getMyCart(): void {
    this.cartService.getMyCart().subscribe((response: Cart) => {
      this.cartItems.set(response.items);
    });
  }

  decreaseQty(item: CartItem): void {
    if (item.quantity > 1) {
      item.quantity -= 1;

      this.cartService.updateCartItem(item.productId, item.quantity).subscribe({
        next: () => {
          this.toastTitle = 'Success';
          this.toastMessage = 'Product updated successfully!';
          this.toastType = 'success';
          this.showToast.set(true);
        },
        error: () => {
          this.toastTitle = 'Error';
          this.toastMessage = 'Failed to update product.';
          this.toastType = 'error';
          this.showToast.set(true);
        },
      });
    }
  }

  increaseQty(item: CartItem): void {
    if (item.quantity < 100) {
      item.quantity += 1;

      this.cartService.updateCartItem(item.productId, item.quantity).subscribe({
        next: () => {
          this.toastTitle = 'Success';
          this.toastMessage = 'Product updated successfully!';
          this.toastType = 'success';
          this.showToast.set(true);
        },
        error: () => {
          this.toastTitle = 'Error';
          this.toastMessage = 'Failed to update product.';
          this.toastType = 'error';
          this.showToast.set(true);
        },
      });
    }
  }

  ngOnInit(): void {
    this.getMyCart();
  }
}
