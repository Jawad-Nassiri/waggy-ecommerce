import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Home } from './home';
import { HomeService } from '../../../services/home.service';
import { of } from 'rxjs';
import { Product } from '../../../models/product';

describe('Home', () => {
  let component: Home;
  let fixture: ComponentFixture<Home>;

  let homeService: {
    getProductsByCategoryId: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    homeService = {
      getProductsByCategoryId: vi.fn(() => of([])),
    };

    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [
        provideRouter([]),
        {
          provide: HomeService,
          useValue: homeService,
        },
      ],
    })
      .overrideComponent(Home, {
        set: { template: '' },
      })
      .compileComponents();

    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load clothing and food products on initialization', () => {
    const clothingProducts = [{ id: 1, name: 'Dog Hoodie', price: 15, stock: 5 }] as Product[];

    const foodProducts = [{ id: 2, name: 'Dog Food', price: 10, stock: 8 }] as Product[];

    homeService.getProductsByCategoryId
      .mockReturnValueOnce(of(clothingProducts))
      .mockReturnValueOnce(of(foodProducts));

    component.ngOnInit();

    expect(homeService.getProductsByCategoryId).toHaveBeenNthCalledWith(1, 1);
    expect(homeService.getProductsByCategoryId).toHaveBeenNthCalledWith(2, 2);
    expect(component.clothingProducts()).toEqual(clothingProducts);
    expect(component.foodProducts()).toHaveLength(1);
  });
});
