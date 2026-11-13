import { Component, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { Toast } from '../toast/toast';
import { HostListener } from '@angular/core';
@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, Toast],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  constructor(
    public authService: AuthService,
    public cartService: CartService,
    private router: Router,
  ) {
    this.cartService.getMyCart().subscribe();
  }

  username = '';
  menuOpen = false;
  isDropdownOpen = false;

  showToast = signal(false);
  toastTitle = '';
  toastMessage = '';
  toastType = '';

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.user-dropdown')) {
      this.isDropdownOpen = false;
    }
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  getUser() {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.username = (user as any).name;
      },
    });
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.showToast.set(true);
        this.toastTitle = 'Success';
        this.toastMessage = 'You logged out successfully!';
        this.toastType = 'success';

        this.router.navigate(["./"]);

        this.cartService.clearCartCount();
      },
    });
  }

  ngOnInit() {
    this.getUser();
  }
}
