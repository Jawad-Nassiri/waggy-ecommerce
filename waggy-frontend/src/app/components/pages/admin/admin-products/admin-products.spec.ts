import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminProducts } from './admin-products';
import { ProductService } from '../../../../services/product.service';
import { CategoryService } from '../../../../services/category.service';
import { ImageUrlService } from '../../../../services/image-url.service';
import { Product } from '../../../../models/product';
import { Category } from '../../../../models/category';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { NgForm } from '@angular/forms';

describe('AdminProducts', () => {
  let component: AdminProducts;
  let fixture: ComponentFixture<AdminProducts>;

  let productService: {
    getProducts: ReturnType<typeof vi.fn>;
    updateProduct: ReturnType<typeof vi.fn>;
    deleteProduct: ReturnType<typeof vi.fn>;
    addProduct: ReturnType<typeof vi.fn>;
  };

  let categoryService: {
    getCategories: ReturnType<typeof vi.fn>;
  };

  let imageUrlService: {
    getImageUrl: ReturnType<typeof vi.fn>;
  };

  const mockProducts = [
    {
      id: 1,
      name: 'Dog Hoodie',
      description: 'Warm hoodie',
      price: 15,
      stock: 5,
      image: 'hoodie.jpg',
      category: { id: 1, name: 'Dog Cloth' },
    },
    {
      id: 2,
      name: 'Dog Food',
      description: 'Healthy food',
      price: 10,
      stock: 8,
      image: 'food.jpg',
      category: { id: 2, name: 'Dog Food' },
    },
  ] as Product[];

  const mockCategories = [
    { id: 1, name: 'Dog Cloth' },
    { id: 2, name: 'Dog Food' },
  ] as Category[];

  beforeEach(async () => {
    productService = {
      getProducts: vi.fn(() => of(mockProducts)),
      updateProduct: vi.fn((_id: number, _data: FormData) => of(mockProducts[0])),
      deleteProduct: vi.fn(() => of({})),
      addProduct: vi.fn((_data: FormData) => of(mockProducts[0])),
    };

    categoryService = {
      getCategories: vi.fn(() => of(mockCategories)),
    };

    imageUrlService = {
      getImageUrl: vi.fn((image: string) => `http://localhost:8080/${image}`),
    };

    await TestBed.configureTestingModule({
      imports: [AdminProducts],
      providers: [
        { provide: ProductService, useValue: productService },
        { provide: CategoryService, useValue: categoryService },
        { provide: ImageUrlService, useValue: imageUrlService },
      ],
    })
      .overrideComponent(AdminProducts, {
        set: { template: '', imports: [] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(AdminProducts);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load products and categories on initialization', () => {
    component.ngOnInit();

    expect(productService.getProducts).toHaveBeenCalled();
    expect(categoryService.getCategories).toHaveBeenCalled();
    expect(component.products()).toEqual(mockProducts);
    expect(component.categories()).toEqual(mockCategories);
  });

  it('should load all products', () => {
    component.getAllProducts();

    expect(productService.getProducts).toHaveBeenCalled();
    expect(component.products()).toEqual(mockProducts);
  });

  it('should load categories', () => {
    component.getCategories();

    expect(categoryService.getCategories).toHaveBeenCalled();
    expect(component.categories()).toEqual(mockCategories);
  });

  it('should open the edit form with a copy of the selected product', () => {
    component.openEdit(mockProducts[0]);

    expect(component.isEditOpen()).toBe(true);
    expect(component.selectedProduct()).toEqual(mockProducts[0]);
    expect(component.selectedProduct()).not.toBe(mockProducts[0]);
    expect(component.selectedFile).toBeNull();
  });

  it('should close the edit form and clear the selected product', () => {
    component.openEdit(mockProducts[0]);
    component.selectedFile = new File(['image'], 'new.jpg');

    component.closeEdit();

    expect(component.isEditOpen()).toBe(false);
    expect(component.selectedProduct()).toBeNull();
    expect(component.selectedFile).toBeNull();
  });

  it('should update a product successfully', () => {
    component.products.set(mockProducts);

    const updatedProduct = {
      ...mockProducts[0],
      name: 'Updated Hoodie',
    } as Product;

    component.updateProduct(1, updatedProduct);

    expect(productService.updateProduct).toHaveBeenCalledWith(1, expect.any(FormData));
    expect(component.products()[0]).toEqual(mockProducts[0]);
    expect(component.showToast()).toBe(true);
    expect(component.toastTitle).toBe('Success');
    expect(component.toastMessage).toBe('Product updated successfully');
    expect(component.toastType).toBe('success');
    expect(component.isEditOpen()).toBe(false);
  });

  it('should include the selected image when updating a product', () => {
    const image = new File(['image'], 'new.jpg');
    component.selectedFile = image;

    component.updateProduct(1, mockProducts[0]);

    const formData = productService.updateProduct.mock.calls[0][1] as FormData;

    expect(formData.get('image')).toBe(image);
    expect(formData.get('product')).toBeInstanceOf(Blob);
  });

  it('should show an error toast when updating a product fails', () => {
    productService.updateProduct.mockReturnValue(throwError(() => ({ error: 'Update failed' })));

    component.updateProduct(1, mockProducts[0]);

    expect(component.showToast()).toBe(true);
    expect(component.toastTitle).toBe('Error');
    expect(component.toastMessage).toBe('Update failed');
    expect(component.toastType).toBe('error');
  });

  it('should return the image URL from the image service', () => {
    expect(component.getImageUrl('hoodie.jpg')).toBe('http://localhost:8080/hoodie.jpg');
    expect(imageUrlService.getImageUrl).toHaveBeenCalledWith('hoodie.jpg');
  });

  it('should open and close the add-product modal', () => {
    component.openAddProductModal();
    expect(component.isAddOpen()).toBe(true);

    component.closeAddProductModal();
    expect(component.isAddOpen()).toBe(false);
  });

  it('should select an image for a new product', () => {
    const image = new File(['image'], 'new.jpg');
    const input = document.createElement('input');
    Object.defineProperty(input, 'files', { value: [image] });

    component.onAddImageSelected({
      target: input,
    } as unknown as Event);

    expect(component.selectedImage()).toBe(image);
  });

  it('should select an image when editing a product', () => {
    const image = new File(['image'], 'new.jpg');
    const input = document.createElement('input');
    Object.defineProperty(input, 'files', { value: [image] });

    component.onFileSelected({
      target: input,
    } as unknown as Event);

    expect(component.selectedFile).toBe(image);
    expect(component.previewUrl()).toContain('blob:');
  });

  it('should ask for confirmation before deleting a product', () => {
    component.deleteProduct(1);

    expect(component.showConfirm()).toBe(true);
    expect(component.confirmMessage).toBe('Are you sure you want to delete this product?');
    expect(productService.deleteProduct).not.toHaveBeenCalled();
  });

  it('should delete a product after confirmation', () => {
    component.products.set(mockProducts);

    component.deleteProduct(1);
    component.onConfirmYes();

    expect(productService.deleteProduct).toHaveBeenCalledWith(1);
    expect(component.products()).toEqual([mockProducts[1]]);
    expect(component.showConfirm()).toBe(false);
    expect(component.confirmAction).toBeNull();
    expect(component.showToast()).toBe(true);
    expect(component.toastTitle).toBe('Success');
    expect(component.toastMessage).toBe('Product deleted successfully');
    expect(component.toastType).toBe('success');
  });

  it('should show an error toast when deleting a product fails', () => {
    productService.deleteProduct.mockReturnValue(throwError(() => ({ error: 'Delete failed' })));

    component.deleteProduct(1);
    component.onConfirmYes();

    expect(component.showToast()).toBe(true);
    expect(component.toastTitle).toBe('Error');
    expect(component.toastMessage).toBe('Delete failed');
    expect(component.toastType).toBe('error');
    expect(component.showConfirm()).toBe(false);
    expect(component.confirmAction).toBeNull();
  });

  it('should add a product successfully', () => {
    component.products.set([]);
    const productForm = {
      resetForm: vi.fn(),
    } as unknown as NgForm;

    const productRequest = {
      name: 'New Hoodie',
      description: 'A new hoodie',
      price: 20,
      stock: 4,
      categoryId: 1,
      image: 'dog.jpg'
    };

    component.addProduct(productRequest, productForm);

    expect(productService.addProduct).toHaveBeenCalledWith(expect.any(FormData));
    expect(component.products()).toEqual([mockProducts[0]]);
    expect(productForm.resetForm).toHaveBeenCalled();
    expect(component.showToast()).toBe(true);
    expect(component.toastTitle).toBe('Success');
    expect(component.toastMessage).toBe('Product added successfully');
    expect(component.toastType).toBe('success');
    expect(component.isAddOpen()).toBe(false);
  });

  it('should include the selected image when adding a product', () => {
    const image = new File(['image'], 'new.jpg');
    component.selectedImage.set(image);

    const productForm = {
      resetForm: vi.fn(),
    } as unknown as NgForm;

    component.addProduct(
      {
        name: 'New Hoodie',
        description: 'A new hoodie',
        price: 20,
        stock: 4,
        categoryId: 1,
        image: 'dog.jpg',
      },
      productForm,
    );

    const formData = productService.addProduct.mock.calls[0][0] as FormData;

    expect(formData.get('image')).toBe(image);
  });

  it('should show an error toast when adding a product fails', () => {
    productService.addProduct.mockReturnValue(throwError(() => ({ error: 'Add failed' })));

    const productForm = {
      resetForm: vi.fn(),
    } as unknown as NgForm;

    component.addProduct(
      {
        name: 'New Hoodie',
        description: 'A new hoodie',
        price: 20,
        stock: 4,
        categoryId: 1,
        image: 'dog.jpg',
      },
      productForm,
    );

    expect(component.showToast()).toBe(true);
    expect(component.toastTitle).toBe('Error');
    expect(component.toastMessage).toBe('Add failed');
    expect(component.toastType).toBe('error');
  });
});
