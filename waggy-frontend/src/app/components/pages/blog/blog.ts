import { Component } from '@angular/core';
import { Banner } from '../../banner/banner';
import { galleryImages } from '../../../data/gallery-images';
import { Gallery } from '../../gallery/gallery';

@Component({
  selector: 'app-blog',
  imports: [Banner, Gallery],
  templateUrl: './blog.html',
  styleUrl: './blog.css',
})
export class Blog {
   galleryImages = galleryImages;
}
