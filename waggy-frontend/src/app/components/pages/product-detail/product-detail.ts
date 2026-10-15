import { Component, signal } from '@angular/core';
import { Banner } from '../../banner/banner';
import { galleryImages } from '../../../data/gallery-images';
import { Gallery } from '../../gallery/gallery';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { ProductService } from '../../../services/product.service';
import { Product } from '../../../models/product';
import { CartService } from '../../../services/cart.service';
import { Toast } from '../../toast/toast';
import { ImageUrlService } from '../../../services/image-url.service';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-product-detail',
  imports: [Banner, Gallery, RouterLink, Toast, DecimalPipe],
  templateUrl: './product-detail.html',
  styleUrl: './product-detail.css',
})
export class ProductDetail {
  constructor(
    private productService: ProductService,
    private route: ActivatedRoute,
    private cartService: CartService,
    private imageUrlService: ImageUrlService,
  ) {}

  product = signal<Product | null>(null);
  productId = 0;
  galleryImages = galleryImages;
  productQty = 1;

  // toast
  type = '';
  title = '';
  message = '';
  showToast = signal(false);

  increaseQuantity(): void {
    if (this.productQty < this.product()!.stock) {
      this.productQty++;
    }
  }

  decreaseQuantity(): void {
    if (this.productQty > 1) {
      this.productQty--;
    }
  }

  getProductById() {
    this.productService.getProductById(this.productId).subscribe((product) => {
      this.product.set(product);
    });
  }

  addToCart(): void {
    if (!this.product()) {
      return;
    }

    this.cartService.addToCart(this.product()!, this.productQty).subscribe({
      next: () => {
        this.cartService.getMyCart().subscribe();
        this.type = 'success';
        this.title = 'Success';
        this.message = 'Product added to cart successfully!';
        this.showToast.set(true);
      },
      error: () => {
        this.type = 'error';
        this.title = 'Error';
        this.message = 'Failed to add product to cart.';
        this.showToast.set(true);
      },
    });
  }

  getImageUrl(image: string): string {
    return this.imageUrlService.getImageUrl(image);
  }

  ngOnInit() {
    this.productId = Number(this.route.snapshot.paramMap.get('id'));
    this.getProductById();
    setTimeout(() => {
      window.scrollTo(0, 300);
    });
  }
}
