import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminProducts } from './admin-products';
import { ProductService } from '../../../../services/product.service';
import { CategoryService } from '../../../../services/category.service';
import { ImageUrlService } from '../../../../services/image-url.service';
import { of } from 'rxjs';

describe('AdminProducts', () => {
  let component: AdminProducts;
  let fixture: ComponentFixture<AdminProducts>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminProducts],
      providers: [
        {
          provide: ProductService,
          useValue: {
            getProducts: () => of([]),
            addProduct: () => of({}),
            updateProduct: () => of({}),
            deleteProduct: () => of({}),
          },
        },
        {
          provide: CategoryService,
          useValue: {
            getCategories: () => of([]),
          },
        },
        {
          provide: ImageUrlService,
          useValue: {
            getImageUrl: (image: string) => image,
          },
        },
      ],
    })
      .overrideComponent(AdminProducts, {
        set: { template: '' },
      })
      .compileComponents();

    fixture = TestBed.createComponent(AdminProducts);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});