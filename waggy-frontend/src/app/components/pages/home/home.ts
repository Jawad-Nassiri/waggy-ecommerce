import { Component, signal } from '@angular/core';
import { Toast } from '../../toast/toast';
import { RouterLink } from '@angular/router';
import { HomeService } from '../../../services/home.service';
import { Product } from '../../../models/product';
import { ProductCard } from '../../product-card/product-card';
import { galleryImages } from '../../../data/gallery-images';
import { Gallery } from '../../gallery/gallery';

@Component({
  selector: 'app-home',
  imports: [Toast, RouterLink, ProductCard, Gallery],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  constructor(private homeService: HomeService) {}

  toastTitle = '';
  toastMessage = '';
  toastType = '';
  showToast = signal(false);

  currentSlide = signal(0);
  clothingProducts = signal<Product[]>([]);
  foodProducts = signal<Product[]>([]);

  galleryImages = galleryImages;

  ngOnInit() {
    const message = history.state?.toastMessage;

    if (message) {
      this.toastTitle = 'Success';
      this.toastMessage = message;
      this.toastType = 'success';
      this.showToast = signal(true);
    }

    this.getProductsByCategoryId(1, 'clothing');
    this.getProductsByCategoryId(2, 'food');

    setInterval(() => {
      this.currentSlide.update((slide) => (slide + 1) % 3);
    }, 3000);
  }

  changeSlide(value: number): void {
    this.currentSlide.set(value);
  }

  getProductsByCategoryId(categoryId: number, type: 'clothing' | 'food') {
    this.homeService.getProductsByCategoryId(categoryId).subscribe({
      next: (response) => {
        if (type === 'clothing') {
          this.clothingProducts.set(response);
        } else {
          const randomProducts = [...response].sort(() => Math.random() - 0.5).slice(0, 8);
          this.foodProducts.set(randomProducts);
        }
      },
    });
  }

  scroll(slider: HTMLElement, direction: 1 | -1): void {
    const card = slider.firstElementChild as HTMLElement;
    const gap = parseFloat(getComputedStyle(slider).columnGap) || 0;
    const maxScroll = slider.scrollWidth - slider.clientWidth;

    if (direction === 1 && slider.scrollLeft >= maxScroll - 5) {
      slider.scrollTo({ left: 0, behavior: 'smooth' });
    } else if (direction === -1 && slider.scrollLeft <= 5) {
      slider.scrollTo({ left: maxScroll, behavior: 'smooth' });
    } else {
      slider.scrollBy({
        left: direction * (card.offsetWidth + gap),
        behavior: 'smooth',
      });
    }
  }
}
