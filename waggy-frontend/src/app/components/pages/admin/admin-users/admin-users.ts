import { Component, signal } from '@angular/core';
import { User } from '../../../../models/user';
import { UserService } from '../../../../services/user.service';
import { FormsModule } from '@angular/forms';
import { Toast } from '../../../toast/toast';
import { ConfirmModal } from '../confirm-modal/confirm-modal';

@Component({
  selector: 'app-admin-users',
  imports: [FormsModule, Toast, ConfirmModal],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.css',
})
export class AdminUsers {
  constructor(private userService: UserService) {}

  users = signal<User[]>([]);
  isEditOpen = signal(false);
  selectedUser = signal<User | null>(null);

  // toast
  showToast = signal(false);
  toastTitle = '';
  toastMessage = '';
  toastType = '';

  showConfirm = signal(false);
  confirmMessage = '';
  confirmAction: (() => void) | null = null;

  onConfirmYes() {
    this.showConfirm.set(false);
    this.confirmAction?.();
    this.confirmAction = null;
  }

  openEdit(user: User) {
    this.selectedUser.set({ ...user });
    this.isEditOpen.set(true);
  }

  closeEdit() {
    this.isEditOpen.set(false);
    this.selectedUser.set(null);
  }

  getUsers() {
    this.userService.getAllUsers().subscribe((res) => {
      this.users.set(res);
    });
  }

  ngOnInit() {
    this.getUsers();
  }

  updateUser(id: number, data: User) {
    if (
      (data.role === 'ADMIN' || data.role === 'USER') &&
      data.role !== this.users().find((user) => user.id === id)?.role
    ) {
      this.confirmMessage = `Are you sure you want to change this user's role to ${data.role}?`;
      this.confirmAction = () => this.updateUserConfirmed(id, data);
      this.showConfirm.set(true);
      return;
    }

    this.updateUserConfirmed(id, data);
  }

  updateUserConfirmed(id: number, data: User) {
    this.userService.updateUser(id, data).subscribe({
      next: (res) => {
        this.users.update((users) => users.map((user) => (user.id === id ? res : user)));

        this.showToast.set(true);
        this.toastTitle = 'Success';
        this.toastMessage = 'User updated successfully';
        this.toastType = 'success';

        this.closeEdit();
      },
      error: (err) => {
        this.showToast.set(true);
        this.toastTitle = 'Error';
        this.toastMessage = err.error;
        this.toastType = 'error';
      },
    });
  }

  deleteUser(id: number) {
    this.confirmMessage = 'Are you sure you want to delete this user?';

    this.confirmAction = () => {
      this.userService.deleteUser(id).subscribe({
        next: () => {
          this.users.update((users) => users.filter((user) => user.id !== id));

          this.showConfirm.set(false);
          this.confirmAction = null;

          this.showToast.set(true);
          this.toastTitle = 'Success';
          this.toastMessage = 'User deleted successfully';
          this.toastType = 'success';
        },
        error: (err) => {
          this.showConfirm.set(false);
          this.confirmAction = null;

          this.showToast.set(true);
          this.toastTitle = 'Error';
          this.toastMessage = err.error;
          this.toastType = 'error';
        },
      });
    };

    this.showConfirm.set(true);
  }
}
