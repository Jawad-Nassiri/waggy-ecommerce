import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-confirm-modal',
  imports: [],
  templateUrl: './confirm-modal.html',
  styleUrl: './confirm-modal.css',
})
export class ConfirmModal {
  isOpen = input(false);
  title = input('Are you sure?');
  message = input('');
  yes = output<void>();
  no = output<void>();

  onYes() {
    this.yes.emit();
  }

  onNo() {
    this.no.emit();
  }
}