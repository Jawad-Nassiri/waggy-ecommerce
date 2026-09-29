import { Component, signal } from '@angular/core';
import { Toast } from '../../toast/toast';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [Toast, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  toastTitle = '';
  toastMessage = '';
  toastType = '';
  showToast = signal(false);

  currentSlide = signal(0);

  ngOnInit() {
    const message = history.state?.toastMessage;

    if (message) {
      this.toastTitle = 'Success';
      this.toastMessage = message;
      this.toastType = 'success';
      this.showToast = signal(true);
    }

    setInterval(() => {
      this.currentSlide.update((slide) => (slide + 1) % 3);
    }, 3000);
  }

  changeSlide(value: number): void {
    this.currentSlide.set(value);
  }
}
