import { Component, signal } from '@angular/core';
import { Banner } from '../../banner/banner';
import { galleryImages } from '../../../data/gallery-images';
import { Gallery } from '../../gallery/gallery';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Toast } from '../../toast/toast';
import { API_URL } from '../../../core/api.config';

@Component({
  selector: 'app-reset-password',
  imports: [Banner, Gallery, RouterLink, FormsModule, Toast],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css',
})
export class ResetPassword {
  constructor(private http: HttpClient, private router: Router) {}
  galleryImages = galleryImages;
  passwordValid = false;
  password = '';

  showToast = signal(false);
  toastTitle = '';
  toastMessage = '';
  toastType = '';

  isSubmitted = signal(false);

  changeEyeIconStatus(event: Event, input: HTMLInputElement): void {
    const icon = event.target as HTMLElement;

    icon.classList.toggle('fa-eye-slash');
    icon.classList.toggle('fa-eye');

    input.type = input.type === 'text' ? 'password' : 'text';
  }

  validatePassword(password: string, input: HTMLInputElement): void {
    this.passwordValid = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z\d]).{6,}$/.test(password);

    input.style.borderColor = password.length === 0 ? '' : this.passwordValid ? 'green' : 'red';
  }

  resetPassword() {
    if (!this.passwordValid) {
      return;
    }

    const token = new URLSearchParams(window.location.search).get('token');

    this.http
      .post(`${API_URL}/auth/reset-password`, {
        token: token,
        newPassword: this.password,
      })
      .subscribe({
        next: () => {
          this.showToast.set(true);
          this.toastTitle = 'Success';
          this.toastMessage = 'Password updated successfully!';
          this.toastType = 'success';

          this.isSubmitted.set(true);

          setTimeout(() => {this.router.navigate(['/login']);}, 1500)


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
