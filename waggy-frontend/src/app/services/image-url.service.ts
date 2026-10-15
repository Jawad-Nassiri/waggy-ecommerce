import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ImageUrlService {
  getImageUrl(image: string): string {
    if (image.startsWith('uploads/')) {
      return `http://localhost:8080/${image}`;
    }

    return `/images/products/${image}`;
  }
}
