import { Component, signal } from '@angular/core';
import { Toast } from '../../toast/toast';

@Component({
  selector: 'app-home',
  imports: [Toast],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  toastTitle = '';
  toastMessage = '';
  toastType = '';
  showToast = signal(false);

  ngOnInit() {
  const message = history.state?.toastMessage;

  if (message) {
    this.toastTitle = 'Success';
    this.toastMessage = message;
    this.toastType = 'success';
    this.showToast = signal(true);
  }
}
}
