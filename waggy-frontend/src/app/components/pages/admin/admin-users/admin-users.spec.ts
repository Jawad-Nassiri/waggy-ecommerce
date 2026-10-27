import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminUsers } from './admin-users';
import { UserService } from '../../../../services/user.service';
import { User } from '../../../../models/user';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

describe('AdminUsers', () => {
  let component: AdminUsers;
  let fixture: ComponentFixture<AdminUsers>;

  let userService: {
    getAllUsers: ReturnType<typeof vi.fn>;
    updateUser: ReturnType<typeof vi.fn>;
    deleteUser: ReturnType<typeof vi.fn>;
  };

  const mockUsers = [
    { id: 1, name: 'User One', email: 'user1@test.com', role: 'USER' },
    { id: 2, name: 'Admin One', email: 'admin@test.com', role: 'ADMIN' },
  ] as User[];

  beforeEach(async () => {
    userService = {
      getAllUsers: vi.fn(() => of(mockUsers)),
      updateUser: vi.fn((_id: number, data: User) => of(data)),
      deleteUser: vi.fn(() => of({})),
    };

    await TestBed.configureTestingModule({
      imports: [AdminUsers],
      providers: [{ provide: UserService, useValue: userService }],
    })
      .overrideComponent(AdminUsers, {
        set: { template: '', imports: [] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(AdminUsers);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load users on initialization', () => {
    component.ngOnInit();

    expect(userService.getAllUsers).toHaveBeenCalled();
    expect(component.users()).toEqual(mockUsers);
  });

  it('should open the edit form with a copy of the selected user', () => {
    component.openEdit(mockUsers[0]);

    expect(component.isEditOpen()).toBe(true);
    expect(component.selectedUser()).toEqual(mockUsers[0]);
    expect(component.selectedUser()).not.toBe(mockUsers[0]);
  });

  it('should close the edit form and clear the selected user', () => {
    component.openEdit(mockUsers[0]);
    component.closeEdit();

    expect(component.isEditOpen()).toBe(false);
    expect(component.selectedUser()).toBeNull();
  });

  it('should update a user without confirmation when the role is unchanged', () => {
    component.users.set(mockUsers);
    const updatedUser = { ...mockUsers[0], name: 'Updated User' } as User;

    component.updateUser(1, updatedUser);

    expect(userService.updateUser).toHaveBeenCalledWith(1, updatedUser);
    expect(component.users()[0]).toEqual(updatedUser);
    expect(component.showToast()).toBe(true);
    expect(component.toastTitle).toBe('Success');
    expect(component.toastMessage).toBe('User updated successfully');
    expect(component.toastType).toBe('success');
    expect(component.isEditOpen()).toBe(false);
  });

  it('should ask for confirmation when changing a user role', () => {
    component.users.set(mockUsers);
    const updatedUser = { ...mockUsers[0], role: 'ADMIN' } as User;

    component.updateUser(1, updatedUser);

    expect(component.showConfirm()).toBe(true);
    expect(component.confirmMessage).toBe(
      "Are you sure you want to change this user's role to ADMIN?",
    );
    expect(userService.updateUser).not.toHaveBeenCalled();
  });

  it('should execute the confirmed role change', () => {
    component.users.set(mockUsers);
    const updatedUser = { ...mockUsers[0], role: 'ADMIN' } as User;

    component.updateUser(1, updatedUser);
    component.onConfirmYes();

    expect(component.showConfirm()).toBe(false);
    expect(component.confirmAction).toBeNull();
    expect(userService.updateUser).toHaveBeenCalledWith(1, updatedUser);
    expect(component.users()[0]).toEqual(updatedUser);
  });

  it('should show an error toast when updating a user fails', () => {
    userService.updateUser.mockReturnValue(throwError(() => ({ error: 'Update failed' })));

    component.updateUserConfirmed(1, mockUsers[0]);

    expect(component.showToast()).toBe(true);
    expect(component.toastTitle).toBe('Error');
    expect(component.toastMessage).toBe('Update failed');
    expect(component.toastType).toBe('error');
  });

  it('should ask for confirmation before deleting a user', () => {
    component.deleteUser(1);

    expect(component.showConfirm()).toBe(true);
    expect(component.confirmMessage).toBe('Are you sure you want to delete this user?');
    expect(userService.deleteUser).not.toHaveBeenCalled();
  });

  it('should delete the user after confirmation', () => {
    component.users.set(mockUsers);

    component.deleteUser(1);
    component.onConfirmYes();

    expect(userService.deleteUser).toHaveBeenCalledWith(1);
    expect(component.users()).toEqual([mockUsers[1]]);
    expect(component.showConfirm()).toBe(false);
    expect(component.confirmAction).toBeNull();
    expect(component.showToast()).toBe(true);
    expect(component.toastTitle).toBe('Success');
    expect(component.toastMessage).toBe('User deleted successfully');
    expect(component.toastType).toBe('success');
  });

  it('should show an error toast when deleting a user fails', () => {
    component.users.set(mockUsers);

    userService.deleteUser.mockReturnValue(throwError(() => ({ error: 'Delete failed' })));

    component.deleteUser(1);
    component.onConfirmYes();

    expect(component.users()).toEqual(mockUsers);
    expect(component.showConfirm()).toBe(false);
    expect(component.confirmAction).toBeNull();
    expect(component.showToast()).toBe(true);
    expect(component.toastTitle).toBe('Error');
    expect(component.toastMessage).toBe('Delete failed');
    expect(component.toastType).toBe('error');
  });
});
