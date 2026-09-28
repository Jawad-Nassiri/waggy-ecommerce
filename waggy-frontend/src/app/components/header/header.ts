import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
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
  ) {
    this.cartService.getMyCart().subscribe();
  }
  menuOpen = false;
  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  ngOnInit() {
    console.log(this.cartService.cartCount());
  }
}
