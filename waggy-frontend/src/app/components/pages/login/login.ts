import { Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Banner } from '../../banner/banner';
import { Gallery } from '../../gallery/gallery';
import { galleryImages } from '../../../data/gallery-images';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Toast } from '../../toast/toast';

@Component({
  selector: 'app-login',
  imports: [RouterLink, Banner, Gallery, FormsModule, Toast],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  galleryImages = galleryImages;

  passwordValid = false;
  emailValid = false;
  showToast = signal(false);
  toastTitle = '';
  toastMessage = '';
  toastType = '';

  changeEyeIconStatus(event: Event, input: HTMLInputElement): void {
    const icon = event.target as HTMLElement;

    icon.classList.toggle('fa-eye-slash');
    icon.classList.toggle('fa-eye');

    input.type = input.type === 'text' ? 'password' : 'text';
  }

  validateEmail(email: string, input: HTMLInputElement): void {
    this.emailValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

    input.style.borderColor = email.length === 0 ? '' : this.emailValid ? 'green' : 'red';
  }

  validatePassword(password: string, input: HTMLInputElement): void {
    this.passwordValid = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z\d]).{6,}$/.test(password);

    input.style.borderColor = password.length === 0 ? '' : this.passwordValid ? 'green' : 'red';
  }

  login(emailInput: HTMLInputElement, passwordInput: HTMLInputElement): void {
    const email = emailInput.value;
    const password = passwordInput.value;

    if (!email || !password || !this.emailValid || !this.passwordValid) {
      return;
    }

    this.authService.login(email, password).subscribe({
      next: (user) => {
        emailInput.value = '';
        passwordInput.value = '';

        this.passwordValid = false;
        this.emailValid = false;

        this.router.navigate(['/'], {
          state: { toastMessage: 'Login successful!' },
        });
      },

      error: (error) => {
        this.toastTitle = 'Error';
        this.toastMessage = error.error || 'Login failed.';
        this.toastType = 'error';
        this.showToast.set(true);
      },
    });
  }
}
