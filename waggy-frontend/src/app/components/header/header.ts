import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { HttpClient } from '@angular/common/http';
@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  constructor(
    public authService: AuthService,
    public cartService: CartService,
    private http: HttpClient
  ) {
    this.cartService.getMyCart().subscribe();
  }

  username = "";
  menuOpen = false;

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  getUser() {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.username = (user as any).name;
      }
    })
  }

  ngOnInit() {
    this.getUser()
  }
}
