import React from 'react';
import "./Item.css"

export default function Store({prod, addToCart, setCurItem}) {
    return (
        <div className="item">
            <img className="product_image" alt="Product" src={prod["productImage"] || "https://upload.wikimedia.org/wikipedia/commons/thumb/4/46/Question_mark_%28black%29.svg/800px-Question_mark_%28black%29.svg.png"}/>
            <div className="text">
                <br/>
                <h2 className="product_title">{prod.productName}</h2>
                <p className="product_description">{prod.productDescription}</p>

                <label className="product_price">Price: </label>
                <input type="text" value={"$" + prod.price} readOnly/>
                <br/>

                <label className="product_stock">Left in stock: </label>
                <input type="text" value={prod.stock} readOnly/>
                <br/>
            </div>
            <button key={prod.productId} className="addToCart" onClick={() => addToCart(prod.productId)}>Add to cart</button>

            <button className="back" onClick={() => setCurItem(null)}>
                Back
            </button>
        </div>
    )
}