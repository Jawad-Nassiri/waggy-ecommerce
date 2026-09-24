import { Component, ChangeDetectorRef } from '@angular/core';
import { Banner } from '../../banner/banner';
import { Product } from '../../../models/product';
import { ProductCard } from '../../product-card/product-card';
import { ProductService } from '../../../services/product.service';

@Component({
  selector: 'app-products',
  imports: [Banner, ProductCard],
  templateUrl: './products.html',
  styleUrl: './products.css',
})
export class Products {
  
  constructor(
    private productService: ProductService,
    private cdr: ChangeDetectorRef,
  ) {}

  products: Product[] = [];
  currentPage = 1;
  productsPerPage = 9;

  selectedCategory = 0;
  selectedMinPrice = 0;
  selectedMaxPrice = 0;

  get paginatedProducts() {
    const start = (this.currentPage - 1) * this.productsPerPage;
    return this.filteredProducts.slice(start, start + this.productsPerPage);
  }

  get totalPages() {
    return Math.ceil(this.filteredProducts.length / this.productsPerPage);
  }

  // this creates
  // total pages = 1 → [1]
  // total pages = 2 → [1, 2]
  // total pages = 3 → [1, 2, 3]
  get pages() {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  goToPage(page: number) {
    this.currentPage = page;
    window.scrollTo({
      top: 250,
      behavior: 'smooth',
    });
  }

  filterByCategory(categoryId: number) {
    this.selectedCategory = categoryId;
  }

  filterByPrice(min: number, max: number) {
    this.selectedMinPrice = min;
    this.selectedMaxPrice = max;
  }

  get filteredProducts() {
    let result = this.products;

    if (this.selectedCategory !== 0) {
      result = result.filter((product) => product.category.id === this.selectedCategory);
    }

    if (this.selectedMinPrice !== 0 || this.selectedMaxPrice !== 0) {
      result = result.filter(
        (product) =>
          product.price >= this.selectedMinPrice && product.price <= this.selectedMaxPrice,
      );
    }

    return result;
  }

  getProducts() {
    this.productService.getProducts().subscribe((products) => {
      this.products = products;
      // update the view after receiving products asynchronously
      this.cdr.detectChanges();
    });
  }

  ngOnInit() {
    this.getProducts();
  }
}
