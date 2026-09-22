import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-toast',
  imports: [],
  templateUrl: './toast.html',
  styleUrl: './toast.css',
})
export class Toast {
  @Input() title = "";
  @Input() message = "";
  @Input() type = "";
  @Output() closed = new EventEmitter<void>();

  ngOnInit(): void {
    setTimeout(() => {
      this.closed.emit();
    }, 3000);
  }
}
