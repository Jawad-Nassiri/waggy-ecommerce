import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { ProductService } from '../../../services/product.service';
import { Products } from './products';

describe('Products', () => {
  let component: Products;
  let fixture: ComponentFixture<Products>;

  beforeEach(async () => {
    const productServiceMock = {
      getProducts: () => of([]),
    };

    await TestBed.configureTestingModule({
      imports: [Products],
      providers: [
        {
          provide: ProductService,
          useValue: productServiceMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Products);
    component = fixture.componentInstance;

    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should paginate products correctly', () => {
    component.products = Array.from({ length: 15 }, (_, i) => ({
      id: i + 1,
      name: `Product ${i + 1}`,
      description: '',
      price: 10,
      stock: 10,
      image: '',
      createdAt: '',
      category: { id: 1 },
    }));

    expect(component.paginatedProducts.length).toBe(9);
    expect(component.paginatedProducts[0].id).toBe(1);

    component.currentPage = 2;

    expect(component.paginatedProducts.length).toBe(6);
    expect(component.paginatedProducts[0].id).toBe(10);
  });

  it('should filter products by category', () => {
    component.products = [
      {
        id: 1,
        name: 'Clothing',
        description: '',
        price: 10,
        stock: 10,
        image: '',
        createdAt: '',
        category: { id: 1 },
      },
      {
        id: 2,
        name: 'Food',
        description: '',
        price: 20,
        stock: 10,
        image: '',
        createdAt: '',
        category: { id: 2 },
      },
    ];

    component.filterByCategory(1);

    expect(component.filteredProducts.length).toBe(1);
    expect(component.filteredProducts[0].category.id).toBe(1);
  });

  it('should filter products by price', () => {
    component.products = [
      {
        id: 1,
        name: 'Cheap',
        description: '',
        price: 5,
        stock: 10,
        image: '',
        createdAt: '',
        category: { id: 1 },
      },
      {
        id: 2,
        name: 'Medium',
        description: '',
        price: 15,
        stock: 10,
        image: '',
        createdAt: '',
        category: { id: 1 },
      },
      {
        id: 3,
        name: 'Expensive',
        description: '',
        price: 25,
        stock: 10,
        image: '',
        createdAt: '',
        category: { id: 1 },
      },
    ];

    component.filterByPrice(10, 20);

    expect(component.filteredProducts.length).toBe(1);
    expect(component.filteredProducts[0].price).toBe(15);
  });

  it('should combine category and price filters', () => {
    component.products = [
      {
        id: 1,
        name: 'Clothing 5',
        description: '',
        price: 5,
        stock: 10,
        image: '',
        createdAt: '',
        category: { id: 1 },
      },
      {
        id: 2,
        name: 'Clothing 15',
        description: '',
        price: 15,
        stock: 10,
        image: '',
        createdAt: '',
        category: { id: 1 },
      },
      {
        id: 3,
        name: 'Food 15',
        description: '',
        price: 15,
        stock: 10,
        image: '',
        createdAt: '',
        category: { id: 2 },
      },
    ];

    component.filterByCategory(1);
    component.filterByPrice(10, 20);

    expect(component.filteredProducts.length).toBe(1);
    expect(component.filteredProducts[0].id).toBe(2);
  });

  it('should calculate total pages after filtering', () => {
    component.products = Array.from({ length: 12 }, (_, i) => ({
      id: i + 1,
      name: `Product ${i + 1}`,
      description: '',
      price: 15,
      stock: 10,
      image: '',
      createdAt: '',
      category: { id: 1 },
    }));

    expect(component.totalPages).toBe(2);

    component.filterByCategory(2);

    expect(component.totalPages).toBe(0);
  });
});
