import { Component, signal } from '@angular/core';
import { Banner } from '../../banner/banner';
import { galleryImages } from '../../../data/gallery-images';
import { Gallery } from '../../gallery/gallery';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { ProductService } from '../../../services/product.service';
import { Product } from '../../../models/product';

@Component({
  selector: 'app-product-detail',
  imports: [Banner, Gallery, RouterLink],
  templateUrl: './product-detail.html',
  styleUrl: './product-detail.css',
})
export class ProductDetail {
  constructor(
    private productService: ProductService,
    private route: ActivatedRoute,
  ) {}

  product = signal<Product | null>(null);
  productId = 0;
  galleryImages = galleryImages;
  productQty = 1;

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

  ngOnInit() {
    this.productId = Number(this.route.snapshot.paramMap.get('id'));
    this.getProductById();
    setTimeout(() => {
      window.scrollTo(0, 300);
    });
  }
}
