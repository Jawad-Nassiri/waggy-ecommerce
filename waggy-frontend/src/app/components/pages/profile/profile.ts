import { Component, signal } from '@angular/core';
import { Banner } from '../../banner/banner';
import { Gallery } from '../../gallery/gallery';
import { galleryImages } from '../../../data/gallery-images';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  imports: [Banner, Gallery],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  user: any = null;
  galleryImages = galleryImages;

  showToast = signal(false);
  toastTitle = '';
  toastMessage = '';
  toastType = '';

  ngOnInit() {
    this.user = this.authService.currentUser();
  }

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