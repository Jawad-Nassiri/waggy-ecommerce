import { Injectable } from '@angular/core';
import { API_URL } from '../core/api.config';

@Injectable({
  providedIn: 'root',
})
export class ImageUrlService {
  getImageUrl(image: string): string {
    if (image.startsWith('uploads/')) {
      return `${API_URL}/${image}`;
    }

    return `/images/${image}`;
  }
}
