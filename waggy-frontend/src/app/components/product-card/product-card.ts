import { Component, Input, signal } from '@angular/core';
import { Product } from '../../models/product';
import { RouterLink } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { Toast } from '../toast/toast';

@Component({
  selector: 'app-product-card',
  imports: [RouterLink,Toast],
  templateUrl: './product-card.html',
  styleUrl: './product-card.css',
})
export class ProductCard {
  constructor(private cartService: CartService) {}

  @Input() product!: Product;

  // toast
  showToast = signal(false);
  toastTitle = '';
  toastMessage = '';
  toastType = '';

  addToCart(event: Event): void {
    event.preventDefault();
    event.stopPropagation();

    this.cartService.addToCart(this.product, 1).subscribe({
      next: () => {
        this.cartService.getMyCart().subscribe();

        this.toastTitle = 'Success';
        this.toastMessage = 'Product added to cart successfully!';
        this.toastType = 'success';
        this.showToast.set(true);
      },
      error: () => {
        this.toastTitle = 'Error';
        this.toastMessage = 'Failed to add product to cart.';
        this.toastType = 'error';
        this.showToast.set(true);
      },
    });
  }
}
