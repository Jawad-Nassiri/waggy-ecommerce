import { Component, signal } from '@angular/core';
import { Banner } from '../../banner/banner';
import { Gallery } from '../../gallery/gallery';
import { NgIf } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { Toast } from '../../toast/toast';
import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  imports: [Banner, Gallery, NgIf, FormsModule, Toast, RouterLink],
  templateUrl: './signup.html',
  styleUrl: './signup.css',
})
export class Signup {
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {
    this.authService.loadCurrentUser();
  }

  passwordValid = false;
  confirmPasswordValid = false;
  nameValid = false;
  emailValid = false;
  showToast = signal(false);
  toastTitle = '';
  toastMessage = '';
  toastType = 'success';

  galleryImages = [
    '/images/gallery/gallery1.jpg',
    '/images/gallery/gallery2.jpg',
    '/images/gallery/gallery3.jpg',
    '/images/gallery/gallery4.jpg',
    '/images/gallery/gallery5.jpg',
    '/images/gallery/gallery6.jpg',
  ];

  changeEyeIconStatus(event: Event, input: HTMLInputElement): void {
    const icon = event.target as HTMLElement;

    icon.classList.toggle('fa-eye-slash');
    icon.classList.toggle('fa-eye');

    input.type = input.type === 'text' ? 'password' : 'text';
  }

  validatePassword(password: string, event: Event, confirmInput: HTMLInputElement): void {
    const input = event.currentTarget as HTMLInputElement;

    this.passwordValid = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z\d]).{6,}$/.test(password);

    input.style.borderColor = password.length === 0 ? '' : this.passwordValid ? 'green' : 'red';

    this.validateConfirmPassword(password, confirmInput);
  }

  validateConfirmPassword(password: string, input: HTMLInputElement): void {
    this.confirmPasswordValid = input.value === password;

    input.style.borderColor = input.value === '' ? '' : input.value === password ? 'green' : 'red';
  }

  validateName(name: string, input: HTMLInputElement): void {
    this.nameValid = name.length >= 3;
    input.style.borderColor = name.length === 0 ? '' : this.nameValid ? 'green' : 'red';
  }

  validateEmail(email: string, input: HTMLInputElement): void {
    this.emailValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

    input.style.borderColor = email.length === 0 ? '' : this.emailValid ? 'green' : 'red';
  }

  signup(
    nameInput: HTMLInputElement,
    emailInput: HTMLInputElement,
    passwordInput: HTMLInputElement,
    confirmPasswordInput: HTMLInputElement,
  ): void {
    const name = nameInput.value;
    const email = emailInput.value;
    const password = passwordInput.value;

    if (
      !name ||
      !email ||
      !password ||
      !this.nameValid ||
      !this.emailValid ||
      !this.passwordValid ||
      !this.confirmPasswordValid
    ) {
      return;
    }

    this.authService.signup(name, email, password).subscribe({
      next: () => {
        this.toastTitle = 'Success';
        this.toastMessage = 'Registration successful!';
        this.toastType = 'success';
        this.showToast.set(true);

        nameInput.value = '';
        emailInput.value = '';
        passwordInput.value = '';
        confirmPasswordInput.value = '';

        this.nameValid = false;
        this.emailValid = false;
        this.passwordValid = false;
        this.confirmPasswordValid = false;

        this.router.navigate(['/'], {
          state: { toastMessage: 'Registration successful!' },
        });
      },

      error: (error) => {
        this.toastTitle = 'Error';
        this.toastMessage = error.error || 'Registration failed.';
        this.toastType = 'error';
        this.showToast.set(true);
      },
    });
  }
}
