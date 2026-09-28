import { Component } from '@angular/core';
import { Banner } from '../../banner/banner';
import { galleryImages } from '../../../data/gallery-images';
import { Gallery } from '../../gallery/gallery';
import { RouterLink } from '@angular/router';


@Component({
  selector: 'app-product-detail',
  imports: [Banner, Gallery, RouterLink],
  templateUrl: './product-detail.html',
  styleUrl: './product-detail.css',
})
export class ProductDetail {
  galleryImages = galleryImages;

  productQty = 1;
  inStock = 10

  increaseQuantity(): void {
    if (this.productQty < this.inStock) {
      this.productQty++;
    }
  }

  decreaseQuantity(): void {
    if (this.productQty > 1) {
      this.productQty--
    }
  }
}
