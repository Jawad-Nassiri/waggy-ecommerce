import { Component } from '@angular/core';
import { Banner } from '../../banner/banner';
import { Gallery } from '../../gallery/gallery';

@Component({
  selector: 'app-signup',
  imports: [Banner, Gallery],
  templateUrl: './signup.html',
  styleUrl: './signup.css',
})
export class Signup {
  galleryImages = [
    '/images/gallery/gallery1.jpg',
    '/images/gallery/gallery2.jpg',
    '/images/gallery/gallery3.jpg',
    '/images/gallery/gallery4.jpg',
    '/images/gallery/gallery5.jpg',
    '/images/gallery/gallery6.jpg',
  ];
}
