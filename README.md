Kaskade
=======

A strategy game


REQUIREMENTS

Java 1.5 minimum


USAGE

Start the jar in the dist folder. Start a new game and have fun! 

Play around with different board sizes, display options, strategy levels (easy, medium, hard), player options, etc.


RULES

- Each round a player can drop one bullet on a field.

- Each field can only contain as many bullets as it has neighboring fields. Thus a corner field can contain only one bullet, a field along the edges only two, and every other field only three.

- Bullets can only be dropped either on empty fields or on those populated by the player's own color.

- If a bullet is being dropped on field that has reached its treshold of allowed bullets, the bullets escape into the neighboring fields. This way neighboring fields are getting taken over and bullets that already are in a neighboring field assume the new color of the entering bullet.

- If a field is full and gets intruded by bullets of a neighboring field, its bullets will escape into its neighboring fields in the same manner as described before. This way a cascade of bullets escaping into their neighboring fields will take place until there is no more overpopulated field left.

- The player of the remaining color on the board wins the game.


