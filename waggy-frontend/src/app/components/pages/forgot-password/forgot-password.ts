import { Component, signal } from '@angular/core';
import { Banner } from '../../banner/banner';
import { galleryImages } from '../../../data/gallery-images';
import { Gallery } from '../../gallery/gallery';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Toast } from '../../toast/toast';
import { API_URL } from '../../../core/api.config';

@Component({
  selector: 'app-forgot-password',
  imports: [Banner, Gallery, RouterLink, FormsModule, Toast],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  constructor(private http: HttpClient) {}

  galleryImages = galleryImages;
  email = '';

  showToast = signal(false);
  toastTitle = '';
  toastMessage = '';
  toastType = '';

  isSubmitted = signal(false);

  onSubmit() {
    this.http
      .post(`${API_URL}/auth/forgot-password`, {
        email: this.email,
      })
      .subscribe({
        next: () => {
          this.showToast.set(true);
          this.toastTitle = 'Success';
          this.toastMessage = 'A reset link has been sent.';
          this.toastType = 'success';

          this.isSubmitted.set(true);
        },
        error: () => {
          this.showToast.set(true);
          this.toastTitle = 'Error';
          this.toastMessage = 'Something went wrong. Please try again.';
          this.toastType = 'error';
        },
      });
  }
}
