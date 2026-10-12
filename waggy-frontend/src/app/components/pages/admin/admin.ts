import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-admin',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class Admin {
  sidebarOpen = false;

  constructor(private router: Router, private authService: AuthService) {}

    logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/'], {
          state: { toastMessage: 'Logout successful!' },
        });
      },
    });
  }
}