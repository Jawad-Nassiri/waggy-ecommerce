import { Component, signal } from '@angular/core';
import { galleryImages } from '../../../data/gallery-images';
import { Gallery } from '../../gallery/gallery';
import { Banner } from '../../banner/banner';

@Component({
  selector: 'app-faq',
  imports: [Gallery, Banner],
  templateUrl: './faq.html',
  styleUrl: './faq.css',
})
export class Faq {
  galleryImages = galleryImages;

  openIndex = signal<number | null>(null);

  toggle(i: number): void {
    this.openIndex.set(this.openIndex() === i ? null : i);
  }

  faqs = [
    {
      q: 'Do I need an account to place an order?',
      a: 'Yes. You need to create an account and log in before placing an order. This allows us to securely manage your cart and orders.',
    },
    {
      q: 'How can I add a product to my cart?',
      a: 'Choose a product and click the “Add to Cart” button. You can then view your cart and change the quantity of your products before placing your order.',
    },
    {
      q: 'What payment methods do you accept?',
      a: 'Waggy uses Stripe for secure online payments. Your payment information is handled securely by Stripe during checkout.',
    },
    {
      q: 'Can I change the quantity of a product in my cart?',
      a: 'Yes. You can increase or decrease the quantity of products directly from your cart before completing your order.',
    },
    {
      q: 'How can I remove products from my cart?',
      a: 'You can remove individual products from your cart or use the “Clear Cart” button to remove all products at once.',
    },
    {
      q: 'How can I find products by category?',
      a: 'You can browse products from different categories, including pet clothing and pet food, from the shop and home page sections.',
    },
    {
      q: 'How do I know if my payment was successful?',
      a: 'After a successful Stripe payment, you are redirected to the payment confirmation page and your order is updated accordingly.',
    },
    {
      q: 'Can I cancel my order?',
      a: 'If you need to cancel an order, please contact Waggy support as soon as possible. Cancellation availability may depend on the current status of your order.',
    },
    {
      q: 'How can I contact Waggy?',
      a: 'You can contact us through the contact page for questions about products, orders, payments, or any other issue with your purchase.',
    },
  ];
}
