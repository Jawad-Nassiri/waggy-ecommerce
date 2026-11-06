import { Component } from '@angular/core';
import { Banner } from '../../banner/banner';
import { galleryImages } from '../../../data/gallery-images';
import { Gallery } from '../../gallery/gallery';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  imports: [Banner, Gallery, RouterLink],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  galleryImages = galleryImages;
}
