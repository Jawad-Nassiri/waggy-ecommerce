import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProductDetail } from './product-detail';
import { ProductService } from '../../../services/product.service';
import { CartService } from '../../../services/cart.service';
import { ImageUrlService } from '../../../services/image-url.service';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { Product } from '../../../models/product';

describe('ProductDetail', () => {
  let component: ProductDetail;
  let fixture: ComponentFixture<ProductDetail>;

  let productService: {
    getProductById: ReturnType<typeof vi.fn>;
  };

  let cartService: {
    addToCart: ReturnType<typeof vi.fn>;
    getMyCart: ReturnType<typeof vi.fn>;
  };

  let imageUrlService: {
    getImageUrl: ReturnType<typeof vi.fn>;
  };

  const mockProduct = {
    id: 1,
    name: 'Dog Hoodie',
    description: 'Warm hoodie',
    price: 15,
    stock: 5,
    image: 'hoodie.jpg',
  } as Product;

  beforeEach(async () => {
    productService = {
      getProductById: vi.fn(() => of(mockProduct)),
    };

    cartService = {
      addToCart: vi.fn(() => of({})),
      getMyCart: vi.fn(() => of({ items: [] })),
    };

    imageUrlService = {
      getImageUrl: vi.fn((image: string) => `http://localhost:8080/${image}`),
    };

    await TestBed.configureTestingModule({
      imports: [ProductDetail],
      providers: [
        provideRouter([]),
        { provide: ProductService, useValue: productService },
        { provide: CartService, useValue: cartService },
        { provide: ImageUrlService, useValue: imageUrlService },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: vi.fn(() => '1'),
              },
            },
          },
        },
      ],
    })
      .overrideComponent(ProductDetail, {
        set: { template: '', imports: [] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(ProductDetail);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the product by ID', () => {
    component.ngOnInit();

    expect(productService.getProductById).toHaveBeenCalledWith(1);
    expect(component.product()).toEqual(mockProduct);
  });

  it('should increase quantity without exceeding stock', () => {
    component.product.set(mockProduct);
    component.productQty = 1;

    component.increaseQuantity();

    expect(component.productQty).toBe(2);
  });

  it('should not increase quantity beyond stock', () => {
    component.product.set({ ...mockProduct, stock: 1 });
    component.productQty = 1;

    component.increaseQuantity();

    expect(component.productQty).toBe(1);
  });

  it('should decrease quantity without going below one', () => {
    component.productQty = 2;

    component.decreaseQuantity();

    expect(component.productQty).toBe(1);

    component.decreaseQuantity();

    expect(component.productQty).toBe(1);
  });

  it('should add the product to cart and show success toast', () => {
    component.product.set(mockProduct);
    component.productQty = 2;

    component.addToCart();

    expect(cartService.addToCart).toHaveBeenCalledWith(mockProduct, 2);
    expect(cartService.getMyCart).toHaveBeenCalled();
    expect(component.showToast()).toBe(true);
    expect(component.title).toBe('Success');
  });

  it('should show an error when adding to cart fails', () => {
    component.product.set(mockProduct);
    cartService.addToCart.mockReturnValue(throwError(() => new Error('Add failed')));

    component.addToCart();

    expect(component.showToast()).toBe(true);
    expect(component.title).toBe('Error');
    expect(component.message).toBe('Failed to add product to cart.');
  });

  it('should not add to cart when no product is loaded', () => {
    component.product.set(null);

    component.addToCart();

    expect(cartService.addToCart).not.toHaveBeenCalled();
  });

  it('should return the image URL from the service', () => {
    expect(component.getImageUrl('hoodie.jpg')).toBe('http://localhost:8080/hoodie.jpg');
    expect(imageUrlService.getImageUrl).toHaveBeenCalledWith('hoodie.jpg');
  });
});
