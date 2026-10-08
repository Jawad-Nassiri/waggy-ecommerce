import { Component, signal, HostListener } from '@angular/core';
import { Banner } from '../../banner/banner';
import { CartService } from '../../../services/cart.service';
import { CartItem } from '../../../models/cart-item';
import { Cart } from '../../../models/cart';
import { Router, RouterLink } from '@angular/router';
import { Toast } from '../../toast/toast';

@Component({
  selector: 'app-cart',
  imports: [Banner, RouterLink, Toast],
  templateUrl: './cart.html',
  styleUrl: './cart.css',
})
export class CartComponent {
  cartItems = signal<CartItem[]>([]);

  // toast
  showToast = signal(false);
  toastTitle = '';
  toastMessage = '';
  toastType = '';

  constructor(
    private cartService: CartService,
    private router: Router,
  ) {}

  getMyCart(): void {
    this.cartService.getMyCart().subscribe((response: Cart) => {
      console.log('CART FROM BACKEND:', response);
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

  removeItem(productId: number): void {
    this.cartService.removeItemFromCart(productId).subscribe({
      next: (response: Cart) => {
        this.cartItems.set(response.items);
        this.toastTitle = 'Success';
        this.toastMessage = 'Product deleted successfully!';
        this.toastType = 'success';
        this.showToast.set(true);
        this.cartService.getMyCart().subscribe();
      },
      error: () => {
        this.toastTitle = 'Error';
        this.toastMessage = 'Failed to delete product.';
        this.toastType = 'error';
        this.showToast.set(true);
      },
    });
  }

  getCartSubtotal(): number {
    return this.cartItems().reduce((total, item) => total + item.price * item.quantity, 0);
  }

  clearCart(): void {
    this.cartService.clearCart().subscribe({
      next: () => {
        this.cartItems.set([]);
        this.toastTitle = 'Success';
        this.toastMessage = 'Cart cleared successfully!';
        this.toastType = 'success';
        this.showToast.set(true);
        this.cartItems.set([]);
      },
      error: () => {
        this.toastTitle = 'Error';
        this.toastMessage = 'Failed to clear cart.';
        this.toastType = 'error';
        this.showToast.set(true);
      },
    });
  }

  checkout(): void {
    const items = this.cartItems().map((item) => ({
      productId: item.productId,
      quantity: item.quantity,
    }));

    this.cartService.createOrder(items).subscribe({
      next: (order) => {
        this.cartService.createPayment(order.id).subscribe({
          next: (response) => {
            window.location.href = response.checkoutUrl;
          },
        });
      },
    });
  }

  ngOnInit(): void {
    this.getMyCart();
  }

  // reload cart when coming back with the back button
  @HostListener('window:pageshow', ['$event'])
  onPageShow(event: PageTransitionEvent): void {
    if (event.persisted) {
      this.getMyCart();
    }
  }
}
