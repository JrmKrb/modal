 p a c k a g e   n e t w o r k ;  
  
 i m p o r t   j a v a . i o . I O E x c e p t i o n ;  
 i m p o r t   j a v a . n e t . I n e t S o c k e t A d d r e s s ;  
 i m p o r t   j a v a . n i o . B y t e B u f f e r ;  
 i m p o r t   j a v a . n i o . c h a n n e l s . D a t a g r a m C h a n n e l ;  
 i m p o r t   j a v a . u t i l . H a s h M a p ;  
 i m p o r t   j a v a . u t i l . S c a n n e r ;  
  
 p u b l i c   c l a s s   U D P   {  
 	 p r i v a t e   H a s h M a p < I n e t S o c k e t A d d r e s s ,   I n t e g e r >   n e t w o r k L i s t   =   n e w   H a s h M a p < I n e t S o c k e t A d d r e s s ,   I n t e g e r > ( ) ;  
 	 p r i v a t e   T h r e a d   l i s t e n e r T h r e a d ;  
 	 p r i v a t e   D a t a g r a m C h a n n e l   s e r v e r S o c k e t ;  
  
 	 p u b l i c   U D P ( )   {  
 	 	 l i s t e n e r T h r e a d   =   l i s t e n e r ;  
 	 	 l i s t e n e r T h r e a d . s t a r t ( ) ;  
 	 }  
  
 	 p u b l i c   v o i d   w h o I s O n l i n e ( )   {  
 	 	 t r y   {  
 	 	 	 s e r v e r S o c k e t   =   D a t a g r a m C h a n n e l . o p e n ( ) ;  
 	 	 	 I n e t S o c k e t A d d r e s s   b r o a d c a s t   =   n e w   I n e t S o c k e t A d d r e s s (  
 	 	 	 	 	 " 2 5 5 . 2 5 5 . 2 5 5 . 2 5 5 " ,   1 2 3 5 7 ) ;  
 	 	 	 s e r v e r S o c k e t . b i n d ( n e w   I n e t S o c k e t A d d r e s s ( 0 ) ) ;  
 	 	 	 s e r v e r S o c k e t . c o n n e c t ( b r o a d c a s t ) ;  
 	 	 	 B y t e B u f f e r   b u f f   =   M e s s a g e . b u f f e r F r o m S t r i n g ( " W H O I S O N L I N E " ) ;  
 	 	 	 b u f f . f l i p ( ) ;  
 	 	 	 s e r v e r S o c k e t . w r i t e ( b u f f ) ;  
 	 	 	 b u f f . c l e a r ( ) ;  
 	 	 }   c a t c h   ( I O E x c e p t i o n   e )   {  
 	 	 	 S y s t e m . o u t . p r i n t l n ( " E R R E U R   P I N G E R " ) ;  
 	 	 	 e . p r i n t S t a c k T r a c e ( ) ;  
 	 	 }  
 	 }  
  
 	 / *  
 	   *   A n s w e r   t o   a   p i n g   r e q u e s t  
 	   * /  
 	 p u b l i c   s t a t i c   v o i d   p i n g A n s w e r ( D a t a g r a m C h a n n e l   s e r v S o c k e t ,  
 	 	 	 I n e t S o c k e t A d d r e s s   r e m o t e )   {  
 	 	 / /   0   s i   l i b r e ,   1   s i   b u s y   T O D O   :   B u s y  
 	 	 B y t e B u f f e r   b u f f   =   B y t e B u f f e r . a l l o c a t e ( 1 ) ;  
 	 	 b u f f . p u t ( ( b y t e )   0 ) ;  
 	 	 b u f f . p u t S h o r t ( 1 ,   ( s h o r t )   0 ) ;  
 	 	 b u f f . f l i p ( ) ;  
 	 	 t r y   {  
 	 	 	 s e r v S o c k e t . s e n d ( b u f f ,   r e m o t e ) ;  
 	 	 }   c a t c h   ( I O E x c e p t i o n   e )   {  
 	 	 	 e . p r i n t S t a c k T r a c e ( ) ;  
 	 	 }  
 	 }  
  
 	 / *  
 	   *   L e   L i s t e n e r   a t t e n d   l e s   r e q u � t e   P i n g   e t   l e s   r � p o n s e s   a u x   p i n g  
 	   * /  
 	 p r i v a t e   T h r e a d   l i s t e n e r   =   n e w   T h r e a d ( )   {  
 	 	 p u b l i c   v o i d   r u n ( )   {  
 	 	 	 B y t e B u f f e r   b u f f   =   B y t e B u f f e r . a l l o c a t e ( 2 5 6 0 0 0 ) ;  
 	 	 	 w h i l e   ( t r u e )   {  
 	 	 	 	 t r y   {  
 	 	 	 	 	 I n e t S o c k e t A d d r e s s   r e m o t e   =   ( I n e t S o c k e t A d d r e s s )   s e r v e r S o c k e t  
 	 	 	 	 	 	 	 . r e c e i v e ( b u f f ) ;  
 	 	 	 	 	 S t r i n g   r e c e i v e d S t r i n g   =   b u f f . a s C h a r B u f f e r ( ) . t o S t r i n g ( ) ;  
 	 	 	 	 	 S y s t e m . o u t . p r i n t l n ( " R e c e i v e d   F R O M   "   +   r e m o t e   +   "   :   "  
 	 	 	 	 	 	 	 +   r e c e i v e d S t r i n g ) ;  
 	 	 	 	 	 i n t   b u s y   =   b u f f . g e t ( ) ;  
 	 	 	 	 	 i n t   n u m b e r T a s k s   =   b u f f . g e t S h o r t ( 1 ) ;  
 	 	 	 	 	 i n t [ ]   t a s k s   =   n e w   i n t [ n u m b e r T a s k s ] ;  
 	 	 	 	 	 f o r   ( i n t   i   =   0 ;   i   <   n u m b e r T a s k s ;   i + + )   {  
 	 	 	 	 	 	 t a s k s [ i ]   =   b u f f . g e t S h o r t ( 3   +   2   *   i ) ;  
 	 	 	 	 	 }  
 	 	 	 	 	 n e t w o r k L i s t . p u t ( r e m o t e ,   b u s y ) ;  
 	 	 	 	 }   c a t c h   ( I O E x c e p t i o n   e )   {  
 	 	 	 	 	 e . p r i n t S t a c k T r a c e ( ) ;  
 	 	 	 	 }  
 	 	 	 }  
 	 	 }  
 	 } ;  
  
 	 / *  
 	   *   C l i e n t s   n e e d   t h i s   t o   d i s p a t c h   t h e   w o r k  
 	   * /  
 	 p u b l i c   H a s h M a p < I n e t S o c k e t A d d r e s s ,   I n t e g e r >   g e t N e t w o r k L i s t ( )   {  
 	 	 r e t u r n   n e t w o r k L i s t ;  
 	 }  
 }  
