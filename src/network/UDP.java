 p a c k a g e   n e t w o r k ;  
  
 i m p o r t   j a v a . i o . I O E x c e p t i o n ;  
 i m p o r t   j a v a . n e t . I n e t S o c k e t A d d r e s s ;  
 i m p o r t   j a v a . n i o . B y t e B u f f e r ;  
 i m p o r t   j a v a . n i o . c h a n n e l s . D a t a g r a m C h a n n e l ;  
  
 p u b l i c   c l a s s   U D P   {  
 	  
 	 p u b l i c   v o i d   w h o I s O n l i n e ( )   {  
 	 	 D a t a g r a m C h a n n e l   s e r v e r S o c k e t ;  
 	 	 t r y   {  
 	 	 	 s e r v e r S o c k e t   =   D a t a g r a m C h a n n e l . o p e n ( ) ;  
 	 	 	 I n e t S o c k e t A d d r e s s   b r o a d c a s t   =   n e w   I n e t S o c k e t A d d r e s s (  
 	 	 	 	 	 " 2 5 5 . 2 5 5 . 2 5 5 . 2 5 5 " ,   1 2 3 5 7 ) ;  
 	 	 	 s e r v e r S o c k e t . b i n d ( b r o a d c a s t ) ;  
 	 	 	 B y t e B u f f e r   b u f f   =   M e s s a g e . b u f f e r F r o m S t r i n g ( " W H O I S O N L I N E " ) ;  
 	 	 	 b u f f . f l i p ( ) ;  
 	 	 	 s e r v e r S o c k e t . w r i t e ( b u f f ) ;  
 	 	 	 b u f f . c l e a r ( ) ;  
 	 	 	 w a i t A n s w e r s ( ) ;  
 	 	 	 s e r v e r S o c k e t . c l o s e ( ) ;  
 	 	 }   c a t c h   ( I O E x c e p t i o n   e )   {  
 	 	 	 S y s t e m . o u t . p r i n t l n ( " E R R E U R   P I N G E R " ) ;  
 	 	 	 e . p r i n t S t a c k T r a c e ( ) ;  
 	 	 }  
  
 	 }  
  
 	 p r i v a t e   v o i d   w a i t A n s w e r s ( )   {  
 	 	  
 	 }  
 	  
 	 p r i v a t e   v o i d   l i s t e n ( )   {  
 	 	  
 	 }  
 	  
 	 p r i v a t e   v o i d   p i n g A n s w e r ( )   {  
 	 	  
 	 }  
 	  
 }  
