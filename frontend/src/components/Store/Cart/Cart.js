import React from 'react';
import "./Cart.css"

export default function Cart({cart, setCart, findProduct, orderCart}) {
    async function removeFromCart(id){
        let val = {...cart};
        delete val[id];
        await setCart({...val});
    }

    async function setCartAmount(id, count){
        let val = {...cart};
        val[id] = count;

        let prod = findProduct(id);
        val[id] = Math.min(val[id], prod.stock)

        await setCart({...cart, [id]: val[id]});
    }

    return Object.keys(cart).length > 0 ? (
        <div>
            <h1>Cart</h1>
            <button className="order_cart" onClick={() => orderCart()}>
                Order
            </button>
            <ul className="cart">
                {Object.entries(cart).map(([id, count]) => {
                    let prod = findProduct(id);

                    if(!prod){
                        return (<button></button>);
                    }

                    return (
                        <button key={id} className="product cart_product" onClick={(e) => {
                            e.stopPropagation()
                           // removeFromCart(id)
                        }}>
                            <h3 className="product_title">{prod.productName}</h3>
                            <img className="product_image" alt="Product" src={prod["productImage"] || "https://upload.wikimedia.org/wikipedia/commons/thumb/4/46/Question_mark_%28black%29.svg/800px-Question_mark_%28black%29.svg.png"}/>

                            <label>Count: </label>
                            <input className="cart_amount" type="number" min="0" max={prod.stock} value={count} placeholder="Count" required onChange={(e) => {
                                e.stopPropagation()
                                if(e.target.value <= 0){
                                    removeFromCart(id)
                                }else {
                                    setCartAmount(id, e.target.value)
                                }
                            }}/>

                            <br/>
                            <label className="product_price">Price: </label>
                            <input type="text" value={"$" + Math.ceil(prod.price * count)} readOnly/>
                        </button>
                    )
                })}
            </ul>

        </div>
    ) : null;
}