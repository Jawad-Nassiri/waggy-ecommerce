import { Component, Input, signal } from '@angular/core';
import { Product } from '../../models/product';
import { RouterLink } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { Toast } from '../toast/toast';
import { ImageUrlService } from '../../services/image-url.service';
import { DecimalPipe } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-product-card',
  imports: [RouterLink, Toast, DecimalPipe],
  templateUrl: './product-card.html',
  styleUrl: './product-card.css',
})
export class ProductCard {
  constructor(
    private cartService: CartService,
    public imageUrlService: ImageUrlService,
    private authService: AuthService,
  ) {}

  @Input() product!: Product;

  // toast
  showToast = signal(false);
  toastTitle = '';
  toastMessage = '';
  toastType = '';


  addToCart(event: Event): void {
    event.preventDefault();
    event.stopPropagation();

    if (!this.authService.currentUser()) {
      this.toastTitle = 'Error';
      this.toastMessage = 'Please log in to continue.';
      this.toastType = 'error';
      this.showToast.set(true);
      return;
    }

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
        this.toastMessage = 'Sorry, available stock limit reached.';
        this.toastType = 'error';
        this.showToast.set(true);
      },
    });
  }

  getImageUrl(image: string): string {
    return this.imageUrlService.getImageUrl(image);
  }
}
